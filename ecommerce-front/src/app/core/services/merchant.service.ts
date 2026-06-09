import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MerchantService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  // --- Store Methods ---
  createStore(store: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/stores`, store);
  }

  getStoresByUserId(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stores/user/${userId}`);
  }

  getStoreById(storeId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/stores/${storeId}`);
  }

  updateStore(storeId: number, store: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/stores/${storeId}`, store);
  }

  // --- Category Methods ---
  createCategory(category: any, storeId: number): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/categories/store/${storeId}`,
      category,
    );
  }

  getCategoriesByStore(storeId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/categories/store/${storeId}`);
  }

  // --- Product Methods ---
  createProduct(product: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/products`, product);
  }

  getProductsByStore(storeId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/products/store/${storeId}`);
  }

  updateProduct(productId: number, product: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/products/${productId}`, product);
  }

  deleteProduct(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${productId}`);
  }

  // --- Theme Methods ---
  getThemeByStore(storeId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/themes/store/${storeId}`);
  }

  updateTheme(storeId: number, theme: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/themes/store/${storeId}`, theme);
  }

  deleteStore(storeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/stores/${storeId}`);
  }
}
