package com.RestCrudExample.demo.ws.userservice;


import com.RestCrudExample.demo.ws.ui.model.request.UserDetailsRequestModel;
import com.RestCrudExample.demo.ws.ui.model.response.UserRest;

public interface UserService {
	UserRest createUser(UserDetailsRequestModel userDetails);
}
