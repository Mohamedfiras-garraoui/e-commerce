import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchantService } from '../../../core/services/merchant.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-theme-customization',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './theme-customization.component.html',
  styleUrls: ['./theme-customization.component.scss'],
})
export class ThemeCustomizationComponent implements OnInit {
  theme = {
    primaryColor: '#007bff',
    secondaryColor: '#6c757d',
    backgroundColor: '#ffffff',
    fontFamily: 'sans-serif',
    storeName: 'Ma Boutique',
  };
  stores: any[] = [];
  selectedStoreId: number | null = null;
  errorMessage = '';
  currentUserId: number | null = null;

  constructor(
    private merchantService: MerchantService,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.currentUserId = this.authService.getUserId();
    this.loadStores();
  }

  loadStores() {
    const userId = this.currentUserId;
    if (!userId) {
      this.errorMessage =
        'Utilisateur connecté introuvable. Veuillez vous reconnecter.';
      return;
    }

    this.merchantService.getStoresByUserId(userId).subscribe({
      next: (data) => {
        this.stores = data;
        if (this.stores.length > 0) {
          // Try to use localStorage storeId first, otherwise use first store
          const storeIdStr = localStorage.getItem('store_id');
          if (storeIdStr) {
            this.selectedStoreId = parseInt(storeIdStr);
          } else {
            this.selectedStoreId = this.stores[0].id;
            // Find the selected store name to update theme.storeName
            const selectedStore = this.stores.find(
              (s) => s.id === this.selectedStoreId,
            );
            if (selectedStore) {
              this.theme.storeName = selectedStore.name;
            }
          }
          this.loadTheme();
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des boutiques:', err);
        this.errorMessage = 'Erreur lors du chargement des boutiques';
      },
    });
  }

  loadTheme() {
    if (this.selectedStoreId) {
      this.merchantService.getThemeByStore(this.selectedStoreId).subscribe({
        next: (data) => {
          if (data) {
            this.theme = {
              primaryColor: data.primaryColor || '#007bff',
              secondaryColor: data.secondaryColor || '#6c757d',
              backgroundColor: data.backgroundColor || '#ffffff',
              fontFamily: data.fontFamily || 'sans-serif',
              storeName: data.storeName || 'Ma Boutique',
            };
          }
          // Also update storeName from the selected store
          const selectedStore = this.stores.find(
            (s) => s.id === this.selectedStoreId,
          );
          if (selectedStore) {
            this.theme.storeName = selectedStore.name;
          }
        },
        error: (err) => {
          console.error('Erreur lors du chargement du thème:', err);
        },
      });
    }
  }

  onStoreChange() {
    if (this.selectedStoreId) {
      localStorage.setItem('store_id', this.selectedStoreId.toString());
      const selectedStore = this.stores.find(
        (s) => s.id === this.selectedStoreId,
      );
      if (selectedStore) {
        this.theme.storeName = selectedStore.name;
      }
      this.loadTheme();
    }
  }

  saveTheme() {
    if (this.selectedStoreId) {
      this.merchantService
        .updateTheme(this.selectedStoreId, this.theme)
        .subscribe({
          next: () => {
            localStorage.setItem('custom_theme', JSON.stringify(this.theme));
            this.errorMessage = '';
          },
          error: (err) => {
            console.error('Erreur lors de la sauvegarde du thème:', err);
            this.errorMessage = 'Erreur lors de la sauvegarde du thème';
          },
        });
    }
  }
}
