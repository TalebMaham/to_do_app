import { Routes } from '@angular/router';

export const routes: Routes = [
  { 
    path: 'auth', 
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) // ✅ Charge `AuthModule` en lazy loading
  },
  { 
    path: 'project', 
    loadChildren: () => import('./project/project.module').then(m => m.ProjectModule) 
  },
  { path: '', redirectTo: '/auth/signin', pathMatch: 'full' } 
];
