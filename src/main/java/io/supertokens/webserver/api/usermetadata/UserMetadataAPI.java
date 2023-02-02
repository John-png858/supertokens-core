/*
 *    Copyright (c) 2021, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

package io.supertokens.webserver.api.usermetadata;

import com.google.gson.JsonObject;
import io.supertokens.Main;
import io.supertokens.exceptions.TenantNotFoundException;
import io.supertokens.pluginInterface.RECIPE_ID;
import io.supertokens.pluginInterface.exceptions.StorageQueryException;
import io.supertokens.pluginInterface.exceptions.StorageTransactionLogicException;
import io.supertokens.usermetadata.UserMetadata;
import io.supertokens.webserver.InputParser;
import io.supertokens.webserver.WebserverAPI;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class UserMetadataAPI extends WebserverAPI {
    private static final long serialVersionUID = -3475605151671191143L;

    public UserMetadataAPI(Main main) {
        super(main, RECIPE_ID.USER_METADATA.toString());
    }

    @Override
    public String getPath() {
        return "/recipe/user/metadata";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String userId = InputParser.getQueryParamOrThrowError(req, "userId", false);
        try {
            JsonObject metadata = UserMetadata.getUserMetadata(this.getConnectionUriDomain(req), this.getTenantId(req),
                    main, userId);
            JsonObject response = new JsonObject();
            response.add("metadata", metadata);
            response.addProperty("status", "OK");
            super.sendJsonResponse(200, response, resp);
        } catch (StorageQueryException | TenantNotFoundException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonObject input = InputParser.parseJsonObjectOrThrowError(req);
        String userId = InputParser.parseStringOrThrowError(input, "userId", false);
        JsonObject update = InputParser.parseJsonObjectOrThrowError(input, "metadataUpdate", false);
        try {
            JsonObject metadata = UserMetadata.updateUserMetadata(this.getConnectionUriDomain(req),
                    this.getTenantId(req), main, userId, update);
            JsonObject response = new JsonObject();
            response.add("metadata", metadata);
            response.addProperty("status", "OK");
            super.sendJsonResponse(200, response, resp);
        } catch (StorageQueryException | StorageTransactionLogicException | TenantNotFoundException e) {
            throw new ServletException(e);
        }
    }
}
