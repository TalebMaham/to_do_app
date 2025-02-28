import { Routes } from '@angular/router';
import { ProjectComponent } from './project.component';
import { AuthGuard } from '../guards/auth.guard';
import { ProjectDetailComponent } from './project-detail/project-detail.component';


export const projectRoutes: Routes = [
    { path: '', component: ProjectComponent, canActivate: [AuthGuard] },
    { path: 'project-detail/:id', component: ProjectDetailComponent }, 
  ];
  