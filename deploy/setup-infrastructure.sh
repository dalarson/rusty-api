#!/usr/bin/env bash
# Creates Azure infrastructure for Rusty Rhino.
# Run once, then configure GitHub Secrets as instructed at the end.
#
# Prerequisites: az login

set -euo pipefail

RESOURCE_GROUP="rusty-rhino-rg"
LOCATION="centralus"
APP_SERVICE_PLAN="rusty-api-plan"
APP_SERVICE_NAME="rusty-api"
STATIC_WEB_APP_NAME="rusty-rhino"
COSMOS_ACCOUNT="rusty-db"
COSMOS_DATABASE="inventory"
STORAGE_ACCOUNT="rustyblob"
SUBSCRIPTION_ID=$(az account show --query id -o tsv)

echo "=== Using subscription: $SUBSCRIPTION_ID ==="
echo "=== Resource group: $RESOURCE_GROUP ==="
echo ""

# ── 1. App Service Plan + App Service (Backend) ──────────────────────
echo "Creating App Service Plan..."
az appservice plan create \
  --name "$APP_SERVICE_PLAN" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --sku B1 \
  --is-linux

echo "Creating App Service..."
az webapp create \
  --name "$APP_SERVICE_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --plan "$APP_SERVICE_PLAN" \
  --runtime "JAVA:17-java17"

echo "Enabling system-assigned managed identity..."
PRINCIPAL_ID=$(az webapp identity assign \
  --name "$APP_SERVICE_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query principalId -o tsv)
echo "Managed identity principal ID: $PRINCIPAL_ID"

# ── 2. Static Web App (Frontend) ─────────────────────────────────────
echo ""
echo "Creating Static Web App..."
az staticwebapp create \
  --name "$STATIC_WEB_APP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --sku Free

SWA_TOKEN=$(az staticwebapp secrets list \
  --name "$STATIC_WEB_APP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query properties.apiKey -o tsv)

SWA_URL=$(az staticwebapp show \
  --name "$STATIC_WEB_APP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query defaultHostname -o tsv)

# ── 3. Role Assignments for Managed Identity ─────────────────────────
echo ""
echo "Assigning Cosmos DB Built-in Data Contributor role..."
az cosmosdb sql role assignment create \
  --account-name "$COSMOS_ACCOUNT" \
  --resource-group "$RESOURCE_GROUP" \
  --scope "/" \
  --principal-id "$PRINCIPAL_ID" \
  --role-definition-id "00000000-0000-0000-0000-000000000002"

echo "Assigning Storage Blob Data Contributor role..."
az role assignment create \
  --assignee "$PRINCIPAL_ID" \
  --role "Storage Blob Data Contributor" \
  --scope "/subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP/providers/Microsoft.Storage/storageAccounts/$STORAGE_ACCOUNT"

# ── 4. App Service Environment Variables ──────────────────────────────
echo ""
echo "Configuring App Service environment variables..."
az webapp config appsettings set \
  --name "$APP_SERVICE_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --settings \
    SPRING_CLOUD_AZURE_COSMOS_ENDPOINT="https://$COSMOS_ACCOUNT.documents.azure.com:443/" \
    SPRING_CLOUD_AZURE_COSMOS_DATABASE="$COSMOS_DATABASE" \
    AZURE_STORAGE_BLOB_ENDPOINT="https://$STORAGE_ACCOUNT.blob.core.windows.net" \
    CORS_ALLOWED_ORIGINS="https://$SWA_URL" \
    ADMIN_USER_1="CHANGE_ME" \
    ADMIN_PASS_1="CHANGE_ME" \
    ADMIN_USER_2="CHANGE_ME" \
    ADMIN_PASS_2="CHANGE_ME" \
    JWT_SECRET="CHANGE_ME_TO_A_STRONG_SECRET_AT_LEAST_32_BYTES"

# ── 5. Federated Credentials for GitHub Actions (OIDC) ───────────────
echo ""
echo "Creating Entra ID app registration for GitHub Actions..."
APP_REG=$(az ad app create --display-name "rusty-api-github-deploy" --query appId -o tsv)
SP_ID=$(az ad sp create --id "$APP_REG" --query id -o tsv)

echo "Assigning Contributor role to service principal..."
az role assignment create \
  --assignee "$SP_ID" \
  --role "Contributor" \
  --scope "/subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP"

TENANT_ID=$(az account show --query tenantId -o tsv)

echo "Creating federated credential for GitHub Actions..."
az ad app federated-credential create \
  --id "$APP_REG" \
  --parameters "{
    \"name\": \"rusty-api-main\",
    \"issuer\": \"https://token.actions.githubusercontent.com\",
    \"subject\": \"repo:dalarson/rusty-api:ref:refs/heads/main\",
    \"audiences\": [\"api://AzureADTokenExchange\"]
  }"

# ── Summary ───────────────────────────────────────────────────────────
echo ""
echo "============================================="
echo "  Infrastructure setup complete!"
echo "============================================="
echo ""
echo "App Service URL:      https://$APP_SERVICE_NAME.azurewebsites.net"
echo "Static Web App URL:   https://$SWA_URL"
echo ""
echo "── GitHub Secrets for dalarson/rusty-api ──"
echo "  AZURE_CLIENT_ID:       $APP_REG"
echo "  AZURE_TENANT_ID:       $TENANT_ID"
echo "  AZURE_SUBSCRIPTION_ID: $SUBSCRIPTION_ID"
echo ""
echo "── GitHub Secrets for dalarson/rusty-rhino ──"
echo "  AZURE_STATIC_WEB_APPS_API_TOKEN: $SWA_TOKEN"
echo ""
echo "── GitHub Variables for dalarson/rusty-rhino ──"
echo "  VITE_RUSTY_API_BASE_URL: https://$APP_SERVICE_NAME.azurewebsites.net"
echo ""
echo "── IMPORTANT: Update App Service settings ──"
echo "  Update ADMIN_USER_1/2, ADMIN_PASS_1/2, and JWT_SECRET"
echo "  in the Azure portal or via: az webapp config appsettings set"
echo "============================================="
