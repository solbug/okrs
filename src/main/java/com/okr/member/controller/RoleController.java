package com.okr.member.controller;

import com.okr.member.service.RoleService;
import com.okr.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Lấy toàn bộ danh sách role
     *
     * @return
     */
    @PreAuthorize("hasRole('Admin')")
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public Response listUser() {
        return Response.success("Get List success").withData(roleService.findAll());
    }
}
