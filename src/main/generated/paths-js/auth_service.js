/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/** @module paths-js/auth_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JAuthService = Java.type('paths.services.auth.AuthService');

/**
The service interface. 
 @class
*/
var AuthService = function(j_val) {

  var j_authService = j_val;
  var that = this;

  /**

   @public
   @param login {string} 
   @param password {string} 
   @param resultHandler {function} 
   */
  this.authenticate = function(login, password, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'function') {
      j_authService["authenticate(java.lang.String,java.lang.String,io.vertx.core.Handler)"](login, password, function(ar) {
      if (ar.succeeded()) {
        resultHandler(ar.result(), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_authService;
};

AuthService._jclass = utils.getJavaClass("paths.services.auth.AuthService");
AuthService._jtype = {
  accept: function(obj) {
    return AuthService._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(AuthService.prototype, {});
    AuthService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
AuthService._create = function(jdel) {
  var obj = Object.create(AuthService.prototype, {});
  AuthService.apply(obj, arguments);
  return obj;
}
module.exports = AuthService;