package com.ideiasmidias.adminuser.service;

import com.ideiasmidias.adminuser.dto.AdminUserResponse;
import com.ideiasmidias.adminuser.dto.CreateAdminUserRequest;
import com.ideiasmidias.adminuser.dto.ResetAdminPasswordRequest;
import com.ideiasmidias.adminuser.dto.UpdateAdminUserRequest;
import com.ideiasmidias.adminuser.dto.UpdateAdminUserStatusRequest;

import java.util.List;

public interface AdminUserService {

    AdminUserResponse createAdmin(CreateAdminUserRequest request);

    List<AdminUserResponse> getAllAdmins();

    AdminUserResponse getAdminById(Long id);

    AdminUserResponse updateAdmin(Long id, UpdateAdminUserRequest request, Long currentAdminId);

    AdminUserResponse updateAdminStatus(Long id, UpdateAdminUserStatusRequest request, Long currentAdminId);

    void resetAdminPassword(Long id, ResetAdminPasswordRequest request, Long currentAdminId);

    void deleteAdmin(Long id, Long currentAdminId);
}