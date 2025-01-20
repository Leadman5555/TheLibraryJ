import {Component, Host, Input, Optional} from '@angular/core';
import {NgIf} from "@angular/common";
import {ControlContainer, FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-image-drop',
    imports: [
        NgIf
    ],
  templateUrl: './image-drop.component.html',
  styleUrl: './image-drop.component.css'
})
export class ImageDropComponent {
  isDropZoneActive: boolean = false;
  imagePreview: string | ArrayBuffer | null = null;
  private readonly maxSize = 2 * 1024 * 1024;

  @Input() parentControl!: FormControl;

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDropZoneActive = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDropZoneActive = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDropZoneActive = false;
    if(event.dataTransfer && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      this.processFile(file);
      event.dataTransfer.clearData();
    }
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if(input.files && input.files[0]) this.processFile(input.files[0]);
  }

  private processFile(file: File): void {
    if (!file.type.startsWith('image/')) {
      alert('Only image types are supported.');
      return;
    }else if (file.size > this.maxSize) {
      alert(`The selected file exceeds the maximum size of ${this.maxSize/(1024*1024)} MB.`);
      return;
    }

    this.parentControl.patchValue(file);
    this.parentControl.updateValueAndValidity();

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(file);
  }

  resetImage(){
    this.parentControl.patchValue(null);
    this.imagePreview = null;
  }
}
