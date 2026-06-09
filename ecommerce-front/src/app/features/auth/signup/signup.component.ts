import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent {
  form = { firstname: '', lastname: '', email: '', password: '' };
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    console.log('Form data to send:', this.form);
    this.authService.signup(this.form).subscribe({
      next: (response) => {
        console.log('Signup response:', response);
        // Rediriger vers la page de connexion après inscription
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Erreur signup:', err);
        // Affiche le message renvoyé par le backend s'il existe
        const serverMsg =
          err && err.error && err.error.message
            ? err.error.message
            : err && err.error
              ? err.error
              : null;
        this.errorMessage =
          serverMsg ||
          'Impossible de créer le compte. Vérifiez les informations.';
      },
    });
  }
}
