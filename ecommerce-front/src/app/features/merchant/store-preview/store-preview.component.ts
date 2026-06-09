import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MerchantService } from '../../../core/services/merchant.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-store-preview',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './store-preview.component.html',
  styleUrls: ['./store-preview.component.scss'],
})
export class StorePreviewComponent implements OnInit {
  stores: any[] = [];
  selectedStore: any = null;
  products: any[] = [];
  categories: any[] = [];
  theme: any = null;
  selectedCategoryId: number | null = null;
  errorMessage = '';
  currentUserId: number | null = null;

  constructor(
    private merchantService: MerchantService,
    private authService: AuthService,
    private route: ActivatedRoute
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
          let initialStoreId = storeIdStr ? parseInt(storeIdStr) : this.stores[0].id;
          
          this.route.paramMap.subscribe(params => {
            const urlStoreId = params.get('storeId');
            if (urlStoreId) {
              initialStoreId = parseInt(urlStoreId);
            }
            this.selectStore(initialStoreId);
          });
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des boutiques:', err);
        this.errorMessage = 'Erreur lors du chargement des boutiques';
      }
    });
  }

  selectStore(storeId: number): void {
    this.selectedStore = this.stores.find(s => s.id === storeId);
    if (this.selectedStore) {
      localStorage.setItem('store_id', storeId.toString());
      this.loadCategories();
      this.loadProducts();
      this.loadTheme();
    }
  }

  loadCategories(): void {
    if (this.selectedStore) {
      this.merchantService.getCategoriesByStore(this.selectedStore.id).subscribe({
        next: (data) => {
          this.categories = data;
        },
        error: (err) => {
          console.error('Erreur lors du chargement des catégories:', err);
        }
      });
    }
  }

  loadProducts(): void {
    if (this.selectedStore) {
      this.merchantService.getProductsByStore(this.selectedStore.id).subscribe({
        next: (data) => {
          this.products = data;
        },
        error: (err) => {
          console.error('Erreur lors du chargement des produits:', err);
        }
      });
    }
  }

  loadTheme(): void {
    if (this.selectedStore) {
      this.merchantService.getThemeByStore(this.selectedStore.id).subscribe({
        next: (data) => {
          this.theme = data;
        },
        error: (err) => {
          console.error('Erreur lors du chargement du thème:', err);
        }
      });
    }
  }

  get filteredProducts(): any[] {
    if (!this.selectedCategoryId) {
      return this.products;
    }
    return this.products.filter(p => p.categoryId === this.selectedCategoryId);
  }

  getCategoryName(categoryId: number | null): string {
    if (!categoryId) return 'Général';
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : 'Général';
  }
}
