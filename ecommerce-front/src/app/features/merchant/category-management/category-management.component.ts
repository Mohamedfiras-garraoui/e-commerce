import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchantService } from '../../../core/services/merchant.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category-management.component.html',
  styleUrls: ['./category-management.component.scss']
})
export class CategoryManagementComponent implements OnInit {
  categories: any[] = [];
  stores: any[] = [];
  selectedStoreId: number | null = null;
  newCategory = { name: '' };
  errorMessage = '';
  currentUserId: number | null = null;

  constructor(
    private merchantService: MerchantService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getUserId();
    this.loadStores();
  }

  loadStores(): void {
    const userId = this.currentUserId;
    if (!userId) {
      this.errorMessage = 'Utilisateur connecté introuvable. Veuillez vous reconnecter.';
      return;
    }

    this.merchantService.getStoresByUserId(userId).subscribe({
      next: (data) => {
        this.stores = data;
        if (this.stores.length > 0) {
          const storeIdStr = localStorage.getItem('store_id');
          if (storeIdStr) {
            this.selectedStoreId = parseInt(storeIdStr);
          } else {
            this.selectedStoreId = this.stores[0].id;
          }
          this.loadCategories();
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des boutiques:', err);
        this.errorMessage = 'Erreur lors du chargement des boutiques';
      }
    });
  }

  loadCategories(): void {
    if (this.selectedStoreId) {
      this.merchantService.getCategoriesByStore(this.selectedStoreId).subscribe({
        next: (data) => {
          this.categories = data;
          this.errorMessage = '';
        },
        error: (err) => {
          console.error('Erreur lors du chargement des catégories:', err);
          this.errorMessage = 'Erreur lors du chargement des catégories';
        }
      });
    }
  }

  onStoreChange(): void {
    if (this.selectedStoreId) {
      localStorage.setItem('store_id', this.selectedStoreId.toString());
      this.loadCategories();
    }
  }

  createCategory(): void {
    if (this.newCategory.name && this.selectedStoreId) {
      this.merchantService.createCategory(this.newCategory, this.selectedStoreId).subscribe({
        next: (data) => {
          this.categories.push(data);
          this.newCategory = { name: '' };
          this.errorMessage = '';
        },
        error: (err) => {
          console.error('Erreur lors de la création de la catégorie:', err);
          this.errorMessage = 'Erreur lors de la création de la catégorie';
        }
      });
    }
  }
}
