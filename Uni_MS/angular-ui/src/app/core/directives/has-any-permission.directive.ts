import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, inject } from '@angular/core';
import { CurrentUserService } from '../../services/current-user.service';

@Directive({
  selector: '[hasAnyPermission]',
  standalone: true
})
export class HasAnyPermissionDirective implements OnInit {
  @Input('hasAnyPermission') permissionCodes: string[] = [];

  private templateRef = inject(TemplateRef<any>);
  private viewContainer = inject(ViewContainerRef);
  private currentUserService = inject(CurrentUserService);

  ngOnInit() {
    if (this.currentUserService.hasAnyPermission(...this.permissionCodes)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
