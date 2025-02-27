import {Component, EventEmitter, Input, Output} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {BookService} from '../../../shared/book-service';
import {textFileTypesValidator} from '@app/shared/functions/fileTypeValidator';
import {AuthorTabDataService} from '../../shared/author-tab-data.service';
import {BookResponse} from '../../../shared/models/book-response';
import {integerValidator} from '../../../book-filter/filterBox/filterValidators';

export function fileNameValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if(control.value === null) return null;
    const files: File[] = control.value;
    if (files.length === 0) return null;
    if(files.find(file => {
      const index = file.name.lastIndexOf('.');
      if(index === -1) return true;
      return file.name.substring(0, index).match(/^([0-9])+(\s-\s(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\s'_"!.-]*)?$/) === null
    })) return {invalidFileName: true};
    return null;
  }
}

@Component({
  selector: 'app-chapter-edit',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './chapter-edit.component.html',
  styleUrl: './chapter-edit.component.css'
})
export class ChapterEditComponent {

  @Output() windowClosed = new EventEmitter<void>();
  @Input() currentBook!: BookResponse;

  chapterUploadForm: FormGroup;

  constructor(private bookService: BookService, private nfb: FormBuilder, private authorTabDataService: AuthorTabDataService) {
    this.chapterUploadForm = this.nfb.group({
      uploadedFiles: [null, [Validators.required, textFileTypesValidator(), fileNameValidator(), Validators.minLength(1), Validators.maxLength(50)]],
    })
  }

  uploadMessage: string  = '';
  uploadErrorMessage: string | null = null;

  isDropZoneActive: boolean = false;

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
      const files = event.dataTransfer.files;
      this.processFiles(files);
      event.dataTransfer.clearData();
    }
  }

  onFileChange(event: any) {
    const files: FileList = event.target.files;
    if (files.length > 0) this.processFiles(files);
  }

  private readonly MAX_FILE_SIZE = 1024*512;

  private processFiles(files: FileList){
    const fileArray = Array.from(files)
      .filter(file => file.size < this.MAX_FILE_SIZE)
      .sort((a, b) => a.name.localeCompare(b.name));
    this.chapterUploadForm.patchValue({
      uploadedFiles: fileArray
    });
    this.chapterUploadForm.get('uploadedFiles')!.updateValueAndValidity();
    if(this.chapterUploadForm.valid) this.updateUploadMessage(fileArray);
  }

  private updateUploadMessage(files: File[]) {
    this.uploadMessage = files.length === 0 ? 'No files uploaded' : files.length === 1 ? `1 chapter to upload. Number: ${this.getUploadedChaptersFormatMessage(files)}` : `${files.length} chapters to upload. Numbers: ${this.getUploadedChaptersFormatMessage(files)}`;
  }

  private getUploadedChaptersFormatMessage(files: File[]): string {
    return files.map(file => {
      const index = file.name.indexOf(' ');
      if(index !== -1) return parseInt(file.name.substring(0, index));
      return  parseInt(file.name);
    }).join(', ')
  }

  attemptChapterUpload(){
    if(this.chapterUploadForm.invalid) return;
    const files: File[] = this.chapterUploadForm.value.uploadedFiles;
      this.bookService.uploadChaptersInBatch(this.currentBook.id, this.authorTabDataService.authorEmail, files).subscribe({
        next: (_) => {
          alert('Upload successful');
        },
        error: (error: string) => {
          this.uploadErrorMessage = error;
        }
      });
  }

  resetForm(){
    this.chapterUploadForm.reset();
    this.uploadErrorMessage = null;
    this.updateUploadMessage([]);
  }

  readonly getFilesControl = () => this.chapterUploadForm.get('uploadedFiles')! as FormControl;

  removeFile(file: File) {
    this.chapterUploadForm.patchValue({
      uploadedFiles: this.chapterUploadForm.get('uploadedFiles')!.value.filter((value: File) => value !== file)
    })
  }

  deleteChapterForm?: FormGroup;
  deleteChapterMessage: string | null = null;

  showDeleteChapterForm() {
    this.deleteChapterForm = this.nfb.group({
      chapterNumber: [0, [Validators.required, Validators.min(1), Validators.max(10000), integerValidator()]],
    });
    console.log(this.deleteChapterForm.errors)
  }

  closeDeleteChapterForm() {
    this.deleteChapterForm = undefined;
  }

  attemptChapterDeletion(){
    if(this.deleteChapterForm!.pristine || this.deleteChapterForm!.invalid) return;
    const chapterNumber = this.deleteChapterForm!.get('chapterNumber')!.value;
    this.bookService.deleteChapters(this.currentBook.id, this.authorTabDataService.authorEmail, chapterNumber).subscribe({
      next: (_) => {
        this.deleteChapterMessage = `Chapter ${chapterNumber} deleted successfully`;
        this.deleteChapterForm!.reset();
      },
      error: (error: string) => {
        this.deleteChapterMessage = error;
      }
    })
  }


  identifyFile(file: File) {
    return file.name;
  }
}
