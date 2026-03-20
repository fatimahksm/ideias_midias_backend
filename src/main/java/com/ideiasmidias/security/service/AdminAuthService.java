package com.ideiasmidias.security.service;

import com.ideiasmidias.security.dto.AdminLoginRequest;
import com.ideiasmidias.security.dto.AdminLoginResponse;

public interface AdminAuthService {

    AdminLoginResponse login(AdminLoginRequest request);
}