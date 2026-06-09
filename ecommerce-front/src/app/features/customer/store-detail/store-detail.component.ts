import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StoreCustomerService } from '../../../core/services/store-customer.service';
import { CartService } from '../../../core/services/cart.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-store-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './store-detail.component.html',
  styleUrl: './store-detail.component.scss',
})
export class StoreDetailComponent implements OnInit {
  storeId!: number;
  store: any;
  products: any[] = [];
  loading = true;
  addingToCart: { [key: number]: boolean } = {};
  quantities: { [key: number]: number } = {};

  constructor(
    private route: ActivatedRoute,
    private storeService: StoreCustomerService,
    private cartService: CartService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.storeId = +this.route.snapshot.paramMap.get('id')!;
    this.loadStore();
    this.loadProducts();
  }

  loadStore(): void {
    this.storeService.getStoreById(this.storeId).subscribe({
      next: (store) => {
        this.store = store;
      },
      error: (err) => {
        console.error('Error loading store:', err);
      },
    });
  }

  loadProducts(): void {
    this.storeService.getProductsByStore(this.storeId).subscribe({
      next: (products) => {
        this.products = products;
        this.products.forEach((product) => {
          this.quantities[product.id] = 1;
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.loading = false;
      },
    });
  }

  decreaseProductQuantity(productId: number): void {
    if (this.quantities[productId] > 1) {
      this.quantities[productId]--;
    }
  }

  increaseProductQuantity(productId: number, stock: number): void {
    if (this.quantities[productId] < stock) {
      this.quantities[productId]++;
    }
  }

  addToCart(productId: number): void {
    this.addingToCart[productId] = true;
    this.cartService
      .addToCart(productId, this.quantities[productId])
      .subscribe({
        next: () => {
          alert('Produit ajouté au panier !');
          this.addingToCart[productId] = false;
        },
        error: (err) => {
          console.error('Error adding to cart:', err);
          alert("Erreur lors de l'ajout au panier");
          this.addingToCart[productId] = false;
        },
      });
  }

  goBack(): void {
    this.router.navigate(['/customer/stores']);
  }

  goToCart(): void {
    this.router.navigate(['/customer/cart']);
  }
}
