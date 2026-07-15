import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuService, MenuItem } from '../../../services/menu.service';
import { PermissionService } from '../../../services/permission.service';
import { SystemSettingService } from '../../../services/system-setting.service';
import { Permission } from '../../../models/permission';

@Component({
  selector: 'app-menus',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './menus.component.html'
})
export class MenusComponent implements OnInit {
  menus: MenuItem[] = [];
  allPermissions: Permission[] = [];
  loading = true;
  showModal = false;
  isEditing = false;
  selectedMenu: any = {};
  modules: string[] = [];

  constructor(private menuService: MenuService, private permissionService: PermissionService, private systemSettingService: SystemSettingService) {}

  ngOnInit() {
    this.loadMenus();
    this.loadPermissions();
    this.loadDropdowns();
  }

  loadDropdowns() {
    this.systemSettingService.getDropdowns().subscribe({
      next: (res) => {
        const data = res?.data || res;
        this.modules = data?.modules || [];
      }
    });
  }

  loadMenus() {
    this.loading = true;
    this.menuService.getAllMenus().subscribe({
      next: (res) => {
        this.menus = res?.data || res || [];
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  loadPermissions() {
    this.permissionService.findAll({ page: 0, size: 500, sortBy: 'id', sortDir: 'asc' }).subscribe({
      next: (res) => { this.allPermissions = res.content || []; }
    });
  }

  openCreate(parentId?: number) {
    this.isEditing = false;
    this.selectedMenu = { title: '', icon: '', route: '', orderNo: 0, permissionCode: '', module: this.modules[0] || 'Security', visible: true, active: true, parentId: parentId || null };
    this.showModal = true;
  }

  openEdit(menu: any) {
    this.isEditing = true;
    this.selectedMenu = { ...menu, parentId: menu.parent?.id || null };
    this.showModal = true;
  }

  save() {
    const payload: any = {
      title: this.selectedMenu.title,
      icon: this.selectedMenu.icon,
      route: this.selectedMenu.route,
      orderNo: this.selectedMenu.orderNo,
      permissionCode: this.selectedMenu.permissionCode,
      module: this.selectedMenu.module,
      visible: this.selectedMenu.visible,
      active: this.selectedMenu.active,
      parent: this.selectedMenu.parentId ? { id: this.selectedMenu.parentId } : null
    };

    if (this.isEditing && this.selectedMenu.id) {
      this.menuService.updateMenu(this.selectedMenu.id, payload).subscribe(() => {
        this.showModal = false;
        this.loadMenus();
      });
    } else {
      this.menuService.createMenu(payload).subscribe(() => {
        this.showModal = false;
        this.loadMenus();
      });
    }
  }

  deleteMenu(id: number) {
    if (confirm('Delete this menu and all children?')) {
      this.menuService.deleteMenu(id).subscribe(() => this.loadMenus());
    }
  }

  toggleExpand(menu: any) {
    menu._expanded = !menu._expanded;
  }
}
