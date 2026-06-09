import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss',
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];
  loading = true;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.cartService.getUserOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading orders:', err);
        this.loading = false;
      },
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING':
        return '#f59e0b';
      case 'PROCESSING':
        return '#3b82f6';
      case 'SHIPPED':
        return '#8b5cf6';
      case 'DELIVERED':
        return '#10b981';
      case 'CANCELLED':
        return '#ef4444';
      default:
        return '#666';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'En attente';
      case 'PROCESSING':
        return 'En cours de traitement';
      case 'SHIPPED':
        return 'Expédié';
      case 'DELIVERED':
        return 'Livré';
      case 'CANCELLED':
        return 'Annulé';
      default:
        return status;
    }
  }

  goToStores(): void {
    this.router.navigate(['/customer/stores']);
  }

  goToCart(): void {
    this.router.navigate(['/customer/cart']);
  }
}
