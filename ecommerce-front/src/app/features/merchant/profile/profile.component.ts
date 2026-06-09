import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-merchant-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class MerchantProfileComponent implements OnInit {
  profile = {
    firstname: '',
    lastname: '',
    email: '',
  };
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.profile = {
      firstname: user.firstname ?? '',
      lastname: user.lastname ?? '',
      email: user.email ?? '',
    };
  }

  saveProfile(): void {
    this.successMessage = 'Profil sauvegardé avec succès !';
    this.errorMessage = '';

    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }
}
