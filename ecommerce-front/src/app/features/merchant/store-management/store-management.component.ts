import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MerchantService } from '../../../core/services/merchant.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-store-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './store-management.component.html',
  styleUrls: ['./store-management.component.scss'],
})
export class StoreManagementComponent implements OnInit {
  stores: any[] = [];
  newStore = { name: '', domain: '', description: '' };
  editingStore: any = null;
  errorMessage = '';
  currentUserId: number | null = null;

  constructor(
    private merchantService: MerchantService,
    private authService: AuthService,
    private router: Router,
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

    console.log('Loading stores for user ID:', userId);
    this.merchantService.getStoresByUserId(userId).subscribe({
      next: (data) => {
        console.log('Stores received from API:', data);
        this.stores = data;
        this.errorMessage = '';
      },
      error: (err) => {
        console.error('Erreur lors du chargement des boutiques:', err);
        this.errorMessage = 'Erreur lors du chargement des boutiques';
      },
    });
  }

  createStore() {
    if (this.newStore.name && this.newStore.domain && this.currentUserId) {
      if (this.editingStore) {
        // Update existing store
        const storeToUpdate = {
          ...this.newStore,
          status: 'ACTIVE',
          owner: { id: this.currentUserId },
        };

        this.merchantService
          .updateStore(this.editingStore.id, storeToUpdate)
          .subscribe({
            next: (data) => {
              const index = this.stores.findIndex((s) => s.id === data.id);
              if (index !== -1) {
                this.stores[index] = data;
              }
              this.resetForm();
              this.errorMessage = '';
            },
            error: (err) => {
              console.error(
                'Erreur lors de la modification de la boutique:',
                err,
              );
              this.errorMessage =
                'Erreur lors de la modification de la boutique';
            },
          });
      } else {
        // Create new store
        const storeToCreate = {
          ...this.newStore,
          status: 'ACTIVE',
          owner: { id: this.currentUserId },
        };

        this.merchantService.createStore(storeToCreate).subscribe({
          next: (data) => {
            this.stores.push(data);
            localStorage.setItem('tenant_id', String(data.id));
            localStorage.setItem('store_id', String(data.id));
            this.newStore = { name: '', domain: '', description: '' };
            this.errorMessage = '';
          },
          error: (err) => {
            console.error('Erreur lors de la création de la boutique:', err);
            this.errorMessage = 'Erreur lors de la création de la boutique';
          },
        });
      }
    }
  }

  editStore(store: any) {
    this.editingStore = store;
    this.newStore = {
      name: store.name,
      domain: store.domain,
      description: store.description,
    };
  }

  deleteStore(store: any) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette boutique?')) {
      this.merchantService.deleteStore(store.id).subscribe({
        next: () => {
          this.stores = this.stores.filter((s) => s.id !== store.id);
          this.errorMessage = '';
        },
        error: (err) => {
          console.error('Erreur lors de la suppression de la boutique:', err);
          this.errorMessage = 'Erreur lors de la suppression de la boutique';
        },
      });
    }
  }

  resetForm() {
    this.editingStore = null;
    this.newStore = { name: '', domain: '', description: '' };
  }

  selectStore(store: any) {
    localStorage.setItem('tenant_id', String(store.id));
    localStorage.setItem('store_id', String(store.id));
    this.router.navigate(['/merchant/preview', store.id]);
  }
}
