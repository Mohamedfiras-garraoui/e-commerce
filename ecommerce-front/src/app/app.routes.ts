import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { SignupComponent } from './features/auth/signup/signup.component';
import { DashboardComponent as AdminDashboard } from './features/admin/dashboard/dashboard.component';
import { MerchantManagementComponent } from './features/admin/merchant-management/merchant-management.component';
import { DashboardComponent as MerchantDashboard } from './features/merchant/dashboard/dashboard.component';
import { StoreManagementComponent } from './features/merchant/store-management/store-management.component';
import { ProductManagementComponent } from './features/merchant/product-management/product-management.component';
import { ThemeCustomizationComponent } from './features/merchant/theme-customization/theme-customization.component';
import { MerchantLayoutComponent } from './features/merchant/merchant-layout/merchant-layout.component';
import { MerchantProfileComponent } from './features/merchant/profile/profile.component';
import { CategoryManagementComponent } from './features/merchant/category-management/category-management.component';
import { StorePreviewComponent } from './features/merchant/store-preview/store-preview.component';
import { CustomerLayoutComponent } from './features/customer/customer-layout/customer-layout.component';
import { StoresListComponent } from './features/customer/stores-list/stores-list.component';
import { StoreDetailComponent } from './features/customer/store-detail/store-detail.component';
import { CartComponent } from './features/customer/cart/cart.component';
import { OrdersComponent } from './features/customer/orders/orders.component';

export const routes: Routes = [
  // Redirige automatiquement l'utilisateur arrivant sur la racine vers l'écran de login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // --- ROUTES AUTHENTIFICATION ---
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },

  // --- ESPACE CLIENT ---
  {
    path: 'customer',
    component: CustomerLayoutComponent,
    children: [
      { path: 'stores', component: StoresListComponent },
      { path: 'store/:id', component: StoreDetailComponent },
      { path: 'cart', component: CartComponent },
      { path: 'orders', component: OrdersComponent },
      { path: '', redirectTo: 'stores', pathMatch: 'full' },
    ],
  },

  // --- ESPACE SUPER ADMIN (Gestion globale de la plateforme) ---
  {
    path: 'super-admin',
    children: [
      { path: 'dashboard', component: AdminDashboard },
      { path: 'merchants', component: MerchantManagementComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },

  // --- ESPACE MERCHANT ADMIN (Gestion des boutiques du marchand) ---
  {
    path: 'merchant',
    component: MerchantLayoutComponent,
    children: [
      { path: 'dashboard', component: MerchantDashboard },
      { path: 'stores', component: StoreManagementComponent },
      { path: 'products', component: ProductManagementComponent },
      { path: 'categories', component: CategoryManagementComponent },
      { path: 'theme', component: ThemeCustomizationComponent },
      { path: 'profile', component: MerchantProfileComponent },
      { path: 'preview', component: StorePreviewComponent },
      { path: 'preview/:storeId', component: StorePreviewComponent },
      { path: '', redirectTo: 'stores', pathMatch: 'full' },
    ],
  },

  // Redirection automatique si l'utilisateur saisit une URL inconnue
  { path: '**', redirectTo: 'login' },
];
