import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  signup(signupData: { username: string; email: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/signup`, signupData);
  }

  signin(loginData: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/signin`, loginData).pipe(
      tap((response) => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('user_id', response.user_id); 
          console.log("voici le user_id : ", localStorage.getItem("user_id"))
        }
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user_id'); // Supprime le token après déconnexion
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token'); // Vérifie si le token est présent
  }
}
