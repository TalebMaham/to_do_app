import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-signup',
  standalone: false,
  templateUrl: './auth-signup.component.html',
  styleUrls: ['./auth-signup.component.css']
})


export class AuthSignupComponent {
  signupForm: FormGroup;
  message: string = '';
  isSuccess: boolean = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router : Router) {
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

onSubmit(): void {
  if (this.signupForm.valid) {
    this.authService.signup(this.signupForm.value).subscribe({
      next: (response) => {
        this.isSuccess = true;
        this.message = response.message || 'Inscription réussie !';
        this.hideToastAfterDelay();

        // Ajouter un délai de 3 secondes avant le logout
        setTimeout(() => {
          this.authService.logout();
          this.router.navigate(['/auth/signin']);
        }, 3000); 
      },
      error: (err) => {
        this.isSuccess = false;
        this.message = 'Erreur lors de l’inscription';
        console.error('Erreur:', err);
        this.hideToastAfterDelay();
      }
    });
  }
}


  private hideToastAfterDelay() {
    setTimeout(() => {
      this.message = '';
    }, 3000); 
  }
}

