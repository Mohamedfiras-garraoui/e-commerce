import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-merchant-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './merchant-layout.component.html',
  styleUrls: ['./merchant-layout.component.scss'],
})
export class MerchantLayoutComponent {
  constructor(public authService: AuthService) {}

  logout(): void {
    this.authService.logout();
    window.location.href = '/login';
  }
}
