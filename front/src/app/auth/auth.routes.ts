import { Routes } from '@angular/router';
import { AuthSignupComponent } from './auth-signup/auth-signup.component';
import { AuthSigninComponent } from './auth-signin/auth-signin.component';

export const authRoutes: Routes = [
  { path: 'signup', component: AuthSignupComponent },
  { path : 'signin', component : AuthSigninComponent}
];
