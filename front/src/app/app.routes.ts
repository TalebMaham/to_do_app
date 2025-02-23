import { Routes } from '@angular/router';

export const routes: Routes = [
  { 
    path: 'auth', 
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) // ✅ Charge `AuthModule` en lazy loading
  },
  { path: '', redirectTo: '/auth/signup', pathMatch: 'full' } // ✅ Redirige par défaut vers l'inscription
];
