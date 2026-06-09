import { HttpInterceptorFn } from '@angular/common/http';

export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  // Récupère l'ID numérique de la boutique sélectionnée pour le merchant
  const currentTenant =
    localStorage.getItem('store_id') || localStorage.getItem('tenant_id') || '';
  const token = localStorage.getItem('token');

  // Clonage de la requête pour ajouter le header Multi-Tenant requis par Spring Boot
  let modifiedReq = req.clone({
    setHeaders: {
      'X-Tenant-ID': currentTenant,
    },
  });

  // Si un token JWT est présent (utilisateur connecté), on l'ajoute à l'en-tête Authorization
  if (token) {
    modifiedReq = modifiedReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(modifiedReq);
};
