
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
  userName : String = ""
  

  // le userName est dans local storage, mais uniquement apres login, comment on peut lui dire de 
  //verifier userName à chaque update ? 

 constructor (private authservice : AuthService, private router : Router){
 }

 ngOnInit() {
  this.userName = localStorage.getItem('user_name') || "";
 }

 logout()
 {
  this.authservice.logout()
  this.router.navigate(['/auth/signin']);
 }


}
