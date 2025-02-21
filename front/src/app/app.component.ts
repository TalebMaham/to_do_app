import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule], // ✅ Ajout de HttpClientModule
  templateUrl: './app.component.html',
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

// ✅ Bootstrap de l'application avec HttpClientModule
bootstrapApplication(AppComponent, {
  providers: [importProvidersFrom(HttpClientModule)] // ✅ Injection correcte
});
