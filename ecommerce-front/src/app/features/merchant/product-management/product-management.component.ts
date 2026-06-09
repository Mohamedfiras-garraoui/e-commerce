import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchantService } from '../../../core/services/merchant.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.scss'],
})
export class ProductManagementComponent implements OnInit {
  products: any[] = [];
  stores: any[] = [];
  categories: any[] = [];
  productForm = {
    id: null as number | null,
    name: '',
    description: '',
    price: 0,
    stock: 10,
    image: '',
    categoryId: null as number | null,
    storeId: null as number | null,
  };
  isEditing = false;
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
          const storeIdStr = localStorage.getItem('store_id');
          const storedStoreId = storeIdStr
            ? Number.parseInt(storeIdStr, 10)
            : null;
          const resolvedStore =
            storedStoreId &&
            this.stores.some((store) => store.id === storedStoreId)
              ? storedStoreId
              : this.stores[0].id;

          this.productForm.storeId = resolvedStore;
          localStorage.setItem('store_id', String(resolvedStore));
          localStorage.setItem('tenant_id', String(resolvedStore));
          this.loadCategoriesAndProducts();
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des boutiques:', err);
        this.errorMessage = 'Erreur lors du chargement des boutiques';
      },
    });
  }

  loadCategoriesAndProducts() {
    if (this.productForm.storeId) {
      this.merchantService
        .getCategoriesByStore(this.productForm.storeId)
        .subscribe({
          next: (data) => {
            this.categories = data;
          },
          error: (err) => {
            console.error('Erreur lors du chargement des catégories:', err);
          },
        });

      this.loadProducts();
    }
  }

  loadProducts() {
    if (this.productForm.storeId) {
      this.merchantService
        .getProductsByStore(this.productForm.storeId)
        .subscribe({
          next: (data) => {
            this.products = data;
          },
          error: (err) => {
            console.error('Erreur lors du chargement des produits:', err);
            this.errorMessage = 'Erreur lors du chargement des produits';
          },
        });
    }
  }

  onStoreChange() {
    if (this.productForm.storeId) {
      localStorage.setItem('store_id', this.productForm.storeId.toString());
      this.loadCategoriesAndProducts();
    }
  }

  saveProduct() {
    if (
      this.productForm.name &&
      this.productForm.price > 0 &&
      this.productForm.storeId
    ) {
      const productData = {
        name: this.productForm.name,
        description: this.productForm.description,
        price: this.productForm.price,
        stock: this.productForm.stock,
        image: this.productForm.image,
        categoryId: this.productForm.categoryId,
        store: { id: this.productForm.storeId },
      };

      if (this.isEditing && this.productForm.id) {
        this.merchantService
          .updateProduct(this.productForm.id, productData)
          .subscribe({
            next: (data) => {
              const index = this.products.findIndex((p) => p.id === data.id);
              if (index !== -1) {
                this.products[index] = data;
              }
              this.resetForm();
              this.errorMessage = '';
            },
            error: (err) => {
              console.error('Erreur lors de la modification du produit:', err);
              this.errorMessage = 'Erreur lors de la modification du produit';
            },
          });
      } else {
        this.merchantService.createProduct(productData).subscribe({
          next: (data) => {
            this.products.push(data);
            this.resetForm();
            this.errorMessage = '';
          },
          error: (err) => {
            console.error('Erreur lors de la création du produit:', err);
            this.errorMessage = 'Erreur lors de la création du produit';
          },
        });
      }
    }
  }

  editProduct(product: any) {
    this.productForm = {
      id: Number(product.id),
      name: product.name,
      description: product.description ?? '',
      price: product.price,
      stock: product.stock,
      image: product.image ?? '',
      categoryId: product.categoryId,
      storeId: this.productForm.storeId,
    };
    this.isEditing = true;
  }

  deleteProduct(id: number) {
    this.merchantService.deleteProduct(id).subscribe({
      next: () => {
        this.products = this.products.filter((p) => p.id !== id);
      },
      error: (err) => {
        console.error('Erreur lors de la suppression du produit:', err);
        this.errorMessage = 'Erreur lors de la suppression du produit';
      },
    });
  }

  resetForm() {
    this.productForm = {
      id: null,
      name: '',
      description: '',
      price: 0,
      stock: 10,
      image: '',
      categoryId: null,
      storeId: this.productForm.storeId,
    };
    this.isEditing = false;
  }

  getCategoryName(categoryId: number | null): string {
    if (!categoryId) return 'Général';
    const category = this.categories.find((c) => c.id === categoryId);
    return category ? category.name : 'Général';
  }

  openImagePicker() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.addEventListener('change', (event) => {
      const file = (event.target as HTMLInputElement).files?.[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.productForm.image = e.target?.result as string;
        };
        reader.readAsDataURL(file);
      }
    });
    input.click();
  }
}
