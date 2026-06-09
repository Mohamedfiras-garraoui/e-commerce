import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  credentials = { email: '', password: '' };
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit(): void {
    this.errorMessage = ''; // Réinitialise l'erreur à chaque tentative

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Connexion réussie ! Token stocké.', response);
        const roles = response?.roles ?? [];
        if (roles.includes('ROLE_MERCHANT')) {
          this.router.navigate(['/merchant/stores']);
          return;
        }

        if (roles.includes('ROLE_SUPER_ADMIN')) {
          this.router.navigate(['/super-admin/dashboard']);
          return;
        }

        this.router.navigate(['/customer/stores']);
      },
      error: (error) => {
        this.errorMessage = 'Email ou mot de passe incorrect.';
        console.error("Détails de l'erreur de connexion :", error);
      },
    });
  }
}
