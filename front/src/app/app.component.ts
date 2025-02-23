
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true,
  imports: [RouterModule,] // ✅ Importer RouterModule pour utiliser <router-outlet>
})
export class AppComponent {
  title = 'Mon Application';
}
