import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StoreCustomerService } from '../../../core/services/store-customer.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stores-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stores-list.component.html',
  styleUrl: './stores-list.component.scss',
})
export class StoresListComponent implements OnInit {
  stores: any[] = [];
  loading = true;

  constructor(
    private storeService: StoreCustomerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStores();
  }

  loadStores(): void {
    this.storeService.getAllStores().subscribe({
      next: (stores) => {
        this.stores = stores;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading stores:', err);
        this.loading = false;
      },
    });
  }

  viewStore(storeId: number): void {
    this.router.navigate(['/customer/store', storeId]);
  }
}
