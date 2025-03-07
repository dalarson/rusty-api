#!/bin/zsh

az cosmosdb sql role assignment create --account-name rusty-rhino --resource-group rusty-rhino --scope "/" --principal-id f0b27bb2-46a2-4b27-9036-d36c1e82c0d0 --role-definition-id /subscriptions/dceea724-651c-48aa-a076-9614d905f73d/resourceGroups/rusty-rhino/providers/Microsoft.DocumentDB/databaseAccounts/rusty-rhino/sqlRoleDefinitions/00000000-0000-0000-0000-000000000002