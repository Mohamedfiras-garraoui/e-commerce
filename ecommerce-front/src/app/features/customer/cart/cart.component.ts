import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent implements OnInit {
  cartItems: any[] = [];
  loading = true;
  checkingOut = false;

  constructor(
    private cartService: CartService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (items) => {
        this.cartItems = items;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading cart:', err);
        this.loading = false;
      },
    });
  }

  getTotal(): number {
    return this.cartItems.reduce((total, item) => {
      return total + item.product.price * item.quantity;
    }, 0);
  }

  decreaseQuantity(item: any): void {
    if (item.quantity > 1) {
      item.quantity = item.quantity - 1;
      this.updateQuantity(item);
    }
  }

  increaseQuantity(item: any): void {
    item.quantity = item.quantity + 1;
    this.updateQuantity(item);
  }

  updateQuantity(item: any): void {
    this.cartService.updateCartItem(item.id, item.quantity).subscribe({
      next: (updatedItem) => {
        if (!updatedItem) {
          this.cartItems = this.cartItems.filter((i) => i.id !== item.id);
        }
      },
      error: (err) => {
        console.error('Error updating quantity:', err);
        alert('Erreur lors de la mise à jour de la quantité');
        this.loadCart();
      },
    });
  }

  removeItem(itemId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet article ?')) {
      this.cartService.removeFromCart(itemId).subscribe({
        next: () => {
          this.cartItems = this.cartItems.filter((item) => item.id !== itemId);
        },
        error: (err) => {
          console.error('Error removing item:', err);
          alert("Erreur lors de la suppression de l'article");
        },
      });
    }
  }

  checkout(): void {
    if (this.cartItems.length === 0) {
      alert('Votre panier est vide !');
      return;
    }

    this.checkingOut = true;
    this.cartService.checkout().subscribe({
      next: (order) => {
        alert(
          'Commande passée avec succès ! Numéro de commande: ' +
            order.orderReference,
        );
        this.checkingOut = false;
        this.router.navigate(['/customer/orders']);
      },
      error: (err) => {
        console.error('Error checking out:', err);
        alert(
          'Erreur lors de la commande: ' + (err.error?.message || err.message),
        );
        this.checkingOut = false;
      },
    });
  }

  goToStores(): void {
    this.router.navigate(['/customer/stores']);
  }

  goToOrders(): void {
    this.router.navigate(['/customer/orders']);
  }
}
