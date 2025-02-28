
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from './auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true,
  imports: [RouterModule,] // ✅ Importer RouterModule pour utiliser <router-outlet>
})
export class AppComponent {
  title = 'Mon Application';

 constructor (private authservice : AuthService, private router : Router){

 }

 logout()
 {
  this.authservice.logout()
  this.router.navigate(['/auth/signin']);
 }


}
