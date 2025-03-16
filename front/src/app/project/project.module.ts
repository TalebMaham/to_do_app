import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ProjectComponent } from './project.component';
import { projectRoutes } from './project.routes';
import { ProjectDetailComponent } from './project-detail/project-detail.component';
import { TaskDetailComponent } from './project-detail/task-detail/task-detail.component';





@NgModule({
  declarations: [
    ProjectComponent,
    ProjectDetailComponent,
    TaskDetailComponent
  ],
  imports: [
    HttpClientModule,
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(projectRoutes),
    FormsModule
  ],
  exports: [
    ProjectComponent,
    ProjectDetailComponent,
    TaskDetailComponent
  ]
})
export class ProjectModule { }