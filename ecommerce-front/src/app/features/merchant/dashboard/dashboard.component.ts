import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MerchantService } from '../../../core/services/merchant.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  totalStores = 0;
  totalProducts = 0;
  currentUserId: number | null = null;

  constructor(
    private merchantService: MerchantService,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.currentUserId = this.authService.getUserId();
    this.loadDashboardData();
  }

  loadDashboardData() {
    const userId = this.currentUserId;
    if (!userId) {
      return;
    }

    this.merchantService.getStoresByUserId(userId).subscribe({
      next: (stores) => {
        this.totalStores = stores.length;

        // Calculate total products across all stores
        let total = 0;
        stores.forEach((store) => {
          this.merchantService.getProductsByStore(store.id).subscribe({
            next: (products) => {
              total += products.length;
              this.totalProducts = total;
            },
          });
        });
      },
    });
  }
}
