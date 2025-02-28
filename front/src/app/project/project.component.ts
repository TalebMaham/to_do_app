import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from './project.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-project',
  standalone : false, 
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css'],
})
export class ProjectComponent implements OnInit {
  projectForm: FormGroup;
  projects: any[] = [];
  message: string = '';
  adminId : any = "" ; 

  constructor(private fb: FormBuilder, private projectService: ProjectService, private router : Router ) {
    this.adminId = localStorage.getItem("user_id")
    this.projectForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', [Validators.required, Validators.minLength(10)]],
      startDate: ['', Validators.required],
      adminId: [+this.adminId, Validators.required] 
    });
  }

  ngOnInit() {
    this.adminId = localStorage.getItem("user_id")
    this.loadPorjectsByAdmin(this.adminId)
  }

  onSubmit() {
    this.adminId = localStorage.getItem("user_id")
    if (this.projectForm.valid) {
      this.projectService.createProject(this.projectForm.value).subscribe(
        (response) => {
          this.message = 'Projet créé avec succès !';
          this.projectForm.reset();
          this.loadPorjectsByAdmin(this.adminId) // Recharger la liste des projets après ajout
        },
        (error) => {
          this.message = 'Erreur lors de la création du projet.';
        }
      );
    }
  }

  loadPorjectsByAdmin(adminId : any) {
    console.log("admin _id  est  : ", adminId)
    if (adminId){
      this.projectService.getProjectsByAdmin(adminId).subscribe(
        (data) => {
          this.projects = data; 
        },
        (error) => {
          console.error('Erreur lors du chargement des projets', error);
        }
      ); 
    }
  }

  loadProjects() {
    this.projectService.getProjects().subscribe(
      (data) => {
        this.projects = data;
      },
      (error) => {
        console.error('Erreur lors du chargement des projets', error);
      }
    );
  }


  viewProjectDetail(projectId: number) {
    this.router.navigate(['/project-detail', projectId]); // Navigation vers la page de détail
  }
}
