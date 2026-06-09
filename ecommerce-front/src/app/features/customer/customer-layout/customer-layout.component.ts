import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customer-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet],
  templateUrl: './customer-layout.component.html',
  styleUrl: './customer-layout.component.scss',
})
export class CustomerLayoutComponent {
  constructor(private authService: AuthService) {}

  logout(): void {
    this.authService.logout();
    window.location.href = '/login';
  }

  getCurrentUser(): any {
    return this.authService.getCurrentUser();
  }
}
