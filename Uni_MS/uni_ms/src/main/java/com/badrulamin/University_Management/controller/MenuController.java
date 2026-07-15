package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Menu;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.repository.MenuRepository;
import com.badrulamin.University_Management.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<?> getMyMenus() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<String> permCodes = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList());

        List<Menu> menus = menuRepository.findAuthorizedMenus(permCodes);
        List<Map<String, Object>> tree = menus.stream().map(this::toTree).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public ResponseEntity<?> findAll() {
        List<Menu> menus = menuRepository.findByParentIsNullOrderByOrderNo();
        List<Map<String, Object>> tree = menus.stream().map(this::toTree).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Menu menu = menuRepository.findById(id).orElse(null);
        if (menu == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResponse.success(toTree(menu)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public ResponseEntity<?> create(@RequestBody Menu menu) {
        Menu saved = menuRepository.save(menu);
        return ResponseEntity.ok(ApiResponse.success("Menu created", toTree(saved)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Menu menu) {
        Menu existing = menuRepository.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();
        menu.setId(id);
        Menu saved = menuRepository.save(menu);
        return ResponseEntity.ok(ApiResponse.success("Menu updated", toTree(saved)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        menuRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu deleted", null));
    }

    private Map<String, Object> toTree(Menu menu) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", menu.getId());
        map.put("title", menu.getTitle());
        map.put("icon", menu.getIcon());
        map.put("route", menu.getRoute());
        map.put("orderNo", menu.getOrderNo());
        map.put("permissionCode", menu.getPermissionCode());
        map.put("module", menu.getModule());
        map.put("visible", menu.getVisible());
        map.put("active", menu.getActive());
        if (menu.getParent() != null) {
            Map<String, Object> parentMap = new LinkedHashMap<>();
            parentMap.put("id", menu.getParent().getId());
            parentMap.put("title", menu.getParent().getTitle());
            map.put("parent", parentMap);
        }
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            map.put("children", menu.getChildren().stream().map(this::toTree).collect(Collectors.toList()));
        }
        return map;
    }
}
