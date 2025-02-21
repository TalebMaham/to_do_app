import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';
import { provideHttpClient } from '@angular/common/http';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: true,
  template: `
    <h1>J'attends le message</h1>
    <h2>{{ message }}</h2>
  `,
})
export class AppComponent {
  message: string = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {
    this.fetchMessage();
  }

  fetchMessage() {
    this.http.get('http://147.93.53.84:8080/api/message', { responseType: 'text' })
      .subscribe(response => {
        this.message = response;
        this.cdr.detectChanges(); // ✅ Mise à jour manuelle du template
      }, error => {
        console.error('Erreur lors de la récupération du message', error);
      });
  }
}

// ✅ Bootstrap de l'application avec provideHttpClient()
bootstrapApplication(AppComponent, {
  providers: [provideHttpClient()] // ✅ Nouvelle façon d'injecter HttpClient
});
