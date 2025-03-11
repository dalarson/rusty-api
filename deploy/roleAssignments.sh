#!/bin/zsh

az cosmosdb sql role assignment create --account-name rusty-rhino --resource-group rusty-rhino --scope "/" --principal-id d4de897e-b869-45a9-85a0-5770b7af6f26 --role-definition-id /subscriptions/dceea724-651c-48aa-a076-9614d905f73d/resourceGroups/rusty-rhino/providers/Microsoft.DocumentDB/databaseAccounts/rusty-rhino/sqlRoleDefinitions/00000000-0000-0000-0000-000000000002