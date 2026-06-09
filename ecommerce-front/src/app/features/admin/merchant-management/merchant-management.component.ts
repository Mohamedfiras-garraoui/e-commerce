import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-merchant-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './merchant-management.component.html',
  styleUrls: ['./merchant-management.component.scss']
})
export class MerchantManagementComponent implements OnInit {
  merchants: any[] = [];
  newMerchant = { firstname: '', lastname: '', email: '', password: '' };
  errorMessage = '';

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.loadMerchants();
  }

  loadMerchants() {
    this.authService.getAllMerchants().subscribe({
      next: (data) => {
        this.merchants = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des marchands:', err);
        this.errorMessage = 'Erreur lors du chargement des marchands';
      }
    });
  }

  addMerchant() {
    if (this.newMerchant.email && this.newMerchant.firstname && this.newMerchant.password) {
      this.authService.createMerchant(this.newMerchant).subscribe({
        next: (data) => {
          console.log('Marchand créé:', data);
          this.merchants.push(data);
          this.newMerchant = { firstname: '', lastname: '', email: '', password: '' };
          this.errorMessage = '';
        },
        error: (err) => {
          console.error('Erreur lors de la création du marchand:', err);
          this.errorMessage = err.error?.message || 'Erreur lors de la création du marchand';
        }
      });
    }
  }

  toggleStatus(merchant: any) {
    this.authService.toggleMerchantStatus(merchant.id).subscribe({
      next: (updatedMerchant) => {
        const index = this.merchants.findIndex(m => m.id === merchant.id);
        if (index !== -1) {
          this.merchants[index] = updatedMerchant;
        }
      },
      error: (err) => {
        console.error('Erreur lors de la modification du statut:', err);
        this.errorMessage = 'Erreur lors de la modification du statut';
      }
    });
  }
}