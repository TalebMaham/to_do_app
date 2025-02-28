import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from '../project.service';


@Component({
  selector: 'app-project-detail',
  standalone : false, 
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css'],
})
export class ProjectDetailComponent implements OnInit {
  project: any;
  newMemberEmail: string = '';
  newMemberRole : string ='';
  message: string = '';

  constructor(private route: ActivatedRoute, private projectService: ProjectService) {}
  ngOnInit() {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));
  
    if (!projectId) {
      console.error("ID du projet non trouvé dans l'URL.");
      this.message = "Erreur : ID du projet manquant.";
      return;
    }
  
    this.loadProjectDetails(projectId);
  }
  

  loadProjectDetails(projectId: number) {
    this.projectService.getProjectById(projectId).subscribe(
      (data) => {
        if (!data) {
          console.error("Aucun projet trouvé avec cet ID !");
          this.message = "Erreur : projet introuvable.";
          return;
        }
  
        this.project = data;
        console.log("Projet chargé :", this.project);
      },
      (error) => {
        console.error('Erreur lors du chargement du projet', error);
        this.message = "Erreur lors du chargement du projet.";
      }
    );
  }
  

  addMember() {
    if (!this.project || !this.project.id) {
      console.error("Le projet n'est pas encore chargé !");
      this.message = "Erreur : projet non chargé.";
      return;
    }
  
    if (!this.newMemberEmail) {
      this.message = "Veuillez entrer une adresse e-mail.";
      return;
    }
  
    this.projectService.addMemberToProject(this.project.id, this.newMemberEmail, this.newMemberRole).subscribe(
      (response) => {
        this.message = 'Membre ajouté avec succès !';
        this.loadProjectDetails(this.project.id); // Recharge la liste des membres
        this.newMemberEmail = '';
      },
      (error) => {
        console.error("Erreur lors de l'ajout du membre :", error);
        this.message = 'Erreur lors de l’ajout du membre.';
      }
    );
  }
  
}
