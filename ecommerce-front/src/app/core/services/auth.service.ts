import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // L'URL doit être relative pour que le proxy Angular intercepte la requête
  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        if (response && response.token) {
          // Stockage des infos de session dans le navigateur
          localStorage.setItem('token', response.token);
          localStorage.setItem('userId', String(response.id ?? ''));
          localStorage.setItem('firstname', response.firstname ?? '');
          localStorage.setItem('lastname', response.lastname ?? '');
          localStorage.setItem('email', response.email ?? '');
          localStorage.setItem('roles', JSON.stringify(response.roles ?? []));
          if (response.storeId) {
            localStorage.setItem('storeId', String(response.storeId));
          }
        }
      }),
    );
  }

  signup(payload: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/signup`, payload);
  }

  // --- Merchant Management Methods ---
  createMerchant(merchant: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/merchant`, merchant);
  }

  getAllMerchants(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/merchants`);
  }

  toggleMerchantStatus(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/merchant/${id}/status`, {});
  }

  logout(): void {
    localStorage.clear();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserId(): number | null {
    const value = localStorage.getItem('userId');
    return value ? Number(value) : null;
  }

  getRoles(): string[] {
    try {
      return JSON.parse(localStorage.getItem('roles') || '[]');
    } catch {
      return [];
    }
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  getCurrentUser() {
    return {
      id: this.getUserId(),
      firstname: localStorage.getItem('firstname'),
      lastname: localStorage.getItem('lastname'),
      email: localStorage.getItem('email'),
      roles: this.getRoles(),
      storeId: localStorage.getItem('storeId'),
    };
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
