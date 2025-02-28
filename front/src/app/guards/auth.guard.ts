import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isLoggedIn()) {
      console.log("Utilisateur connecté :", this.authService.isLoggedIn());
      return true;
    } else {
      console.log("Utilisateur non connecté :", this.authService.isLoggedIn());
      this.router.navigate(['/auth/signin']);
      return false;
    }
  }
}
