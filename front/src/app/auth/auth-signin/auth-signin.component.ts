import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-signin',
  standalone: false,
  templateUrl: './auth-signin.component.html',
  styleUrls: ['./auth-signin.component.css']
})
export class AuthSigninComponent {
  signinForm: FormGroup;
  message: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router : Router ) {
    this.signinForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.signinForm.valid) {
      this.authService.signin(this.signinForm.value).subscribe({
        next: (response) => {
          this.message = response.message || 'Connexion réussie !';
          localStorage.setItem('token', response.token);
          localStorage.setItem('user_name', response.user_name);  // ⚠️ À sécuriser en production
          console.log("Est connecté :", this.authService.isLoggedIn())
          this.router.navigate(['/project']);
        },
        error: (err) => {
          this.message = err.error.error || 'Erreur lors de la connexion';
          console.error('Erreur:', err);
        }
      });
    }
  }
}
