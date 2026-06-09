import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StoreCustomerService {
  private apiUrl = '/api/stores';
  private productApiUrl = '/api/products';

  constructor(private http: HttpClient) {}

  getAllStores(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getStoreById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getProductsByStore(storeId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.productApiUrl}/store/${storeId}`);
  }

  getProductById(id: number): Observable<any> {
    return this.http.get<any>(`${this.productApiUrl}/${id}`);
  }
}
