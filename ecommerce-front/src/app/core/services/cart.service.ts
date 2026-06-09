import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = '/api/cart';
  private orderApiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  getCart(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  addToCart(productId: number, quantity: number = 1): Observable<any> {
    return this.http.post<any>(this.apiUrl, { productId, quantity });
  }

  updateCartItem(cartItemId: number, quantity: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${cartItemId}`, { quantity });
  }

  removeFromCart(cartItemId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${cartItemId}`);
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.apiUrl);
  }

  checkout(): Observable<any> {
    return this.http.post<any>(this.orderApiUrl, {});
  }

  getUserOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.orderApiUrl}/user`);
  }
}
