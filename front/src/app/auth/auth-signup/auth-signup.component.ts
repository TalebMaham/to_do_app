import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-auth-signup',
  standalone : false,
  templateUrl: './auth-signup.component.html',
  styleUrls: ['./auth-signup.component.css']
})
export class AuthSignupComponent {
  signupForm: FormGroup;
  message: string = '';
  private apiUrl = 'http://localhost:8080/api/auth/signup'; // ✅ Remplace par ton URL backend

  constructor(private fb: FormBuilder, private http: HttpClient) { 
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.http.post<any>(this.apiUrl, this.signupForm.value).subscribe({
        next: (response) => {
          this.message = response.message || 'Inscription réussie !';
        },
        error: (err) => {
          this.message = 'Erreur lors de l’inscription';
          console.error('Erreur:', err);
        }
      });
    }
  }
}
