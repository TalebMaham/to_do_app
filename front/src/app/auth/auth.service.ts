import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // ✅ Fournir globalement le service pour éviter de l'ajouter dans un module
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth/signup';

  constructor(private http: HttpClient) { } // ✅ Injection correcte

  registerUser(userData: { username: string; email: string; password: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, userData);
  }
}
