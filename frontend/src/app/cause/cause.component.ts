import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { AlertService } from '../alert-service';
import { Cause } from '../cause';
import { Observable } from 'rxjs';
import {map, startWith} from "rxjs/operators";

@Component({
  selector: 'app-cause',
  templateUrl: './cause.component.html',
  styleUrls: ['./cause.component.css']
})

export class CauseComponent implements OnInit {
  private causeTypes = ['Children', 'Adults', 'Homeless', 'Animals', 'Nature'];
  causeForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;
  selectedFiles: FileList;  
  currentFileUpload: File;
  filteredLocations: Observable<string[]>;
  locations: string[] = [" Белослав"," Бяла"," Вълчидол"," Девня"," Долни Чифлик"," Дългопол",
    " Летница"," Луковит"," Провадия"," Суворово"," Тетевен"," Троян"," Угърчин"," Ябланица","Айтос","Алфатар","Антоново","Ардино","Асеновд",
    "Ахтопол","Балчик","Банкя","Банско","Баня","Батак","Батановци","Белене","Белица","Белово","Белодчик","Берковица","Бобовдол","Бобошево",
    "Бойчиновци","Болярово","Борово","Ботевд","Брацигово","Брегово","Брезник","Брезово","Брусарци","Бухово","Българово","Бяла","Бяла Слатина",
    "Бяла Черква","Велики Преслав","Велинд","Ветово","Вълчедръм","Върбица","Вършец","Генерал-Тошево","Главиница","Годеч","Горна Оряховица",
    "Гоце Делчев","мада","Гулянци","Гурково","Гълъбово","Две могили","Дебелец","Девин","Девин (Настан)","Джебел","Димитровд","Димово","Долна Баня",
    "Долна Митрополия","Долна Оряховица","Долни Дъбник","Доспат","Драгоман","Дряново","Дулово","Дунавци","Дупница","Елена","Елин Пелин","Елхово",
    "Етрополе","Завет","Земен","Златарица","Златица","Златод","Ивайловд","Искър","Исперих","Ихтиман","Каварна","Казанлък","Калофер","Камено",
    "Каолиново","Карлово","Карнобат","Каспичан","Кермен","Килифарево","Китен","Клисура","Кнежа","Козлодуй","Койнаре","Копривщица","Костенец",
    "Костинброд","Котел","Кочериново","Кресна","Криводол","Кричим","Крумовд","Кубрат","Кула","Левски","Лозница","Лом","Лъки","Любимец",
    "Лясковец","Мадан","Маджарово","Малко Търново","Мездра","Мелник","Меричлери","Мизия","Момчилд","Мъглиж","Неделино","Несебър","Николаево",
    "Никопол","Нова Загора","Нови Искър","Нови Пазар","Обзор","Омуртаг","Опака","Оряхово","Павел Баня","Павликени","Панагюрище","Перущица",
    "Петрич","Пещера","Пирдоп","Плачковци","Плиска","Полски Тръмбеш","Поморие","Попово","Правец","Приморско","Първомай","Раднево","Радомир",
    "Разлог","Ракитово","Раковски","Рила","Роман","Рудозем","Русе","Садово","Самоков","Сандански","Сапарева Баня","Свиленд","Свищов","Своге",
    "Севлиево","Сеново","Септември","Симеоновд","Симитли","Славяново","Славяново","Сливница","Смядово","Созопол","Сопот","Средец","Стамболийски",
    "Стражица","Стралджа","Стрелча","Сунгурларе","Сухиндол","Съединение","Твърдица","Тервел","Тополовд","Трън","Тръстеник","Трявна","Тутракан",
    "Харманли","Хисаря","Цар Калоян","Царево","Чепеларе","Червен Бряг","Чипровци","Чирпан","Шабла","Шивачево","Шипка","курортен комплекс Златни Пясъци",
    "курортен комплекс Св.Константин и Елена","курортен комплекс Слънчев Ден","курортен комплекс Албена","курортен комплекс Боровец",
    "курортен комплекс Дюни","курортен комплекс Елените","курортен комплекс Пампорово","курортен комплекс Слънчев Бряг","курортен комплекс Цигов Чарк"];

  constructor(
    private route: ActivatedRoute, 
    private router: Router, 
    private causeService: CauseService,
    private formBuilder: FormBuilder,
    private alertService: AlertService) {
  }

  ngOnInit() {
    this.causeForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      date: ['', Validators.required],
      owner: localStorage.getItem('username'),
      causeType: ['', Validators.required],
      photoPath: new FormControl()
    });
    this.filteredLocations = this.causeForm.get('location').valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }
  _filter(value: string): string[] {
    const filterValue = this._normalizeValue(value);
    return this.locations.filter(loc => this._normalizeValue(loc).includes(filterValue));
  }

  private _normalizeValue(value: string): string {
    return value.toLowerCase().replace(/\s/g, '');
  }

  get f() { return this.causeForm.controls; }

  private goToCausesList() {
    this.router.navigate(['/causes']);
  }

  selectFile(event) {
    const file = event.target.files.item(0);
    var size = event.target.files[0].size;  
    if(size > 5000000) {  
      alert("size must not exceeds 5 MB");  
      this.causeForm.get('causeImage').setValue("");  
    }  
    else {  
      this.selectedFiles = event.target.files;  
    }
  }

  OnSubmit() {
    this.submitted = true;
    if (!this.causeForm.valid) {
      return
    }
    var name = this.causeForm.get('name').value;
    var location = this.causeForm.get('location').value;
    var description = this.causeForm.get('description').value;
    var causeType = this.causeForm.get('causeType').value;
    var time = this.causeForm.get('date').value.getTime() / 1000;
    var imagePath: string;
    var c = new Cause({
      name: name,
      location: location,
      description: description,
      causeType: causeType,
      time: time,
      imagePath: imagePath,
      owner: localStorage.getItem('username')
    });

    this.causeService.save(c).subscribe(
      cause => {
        if(this.selectedFiles != null) {
          this.causeService.uploadImage(this.selectedFiles.item(0), cause.id).subscribe(res => {
            this.alertService.success("Успешно създадохте кауза!");
            this.goToCausesList();
          }, err => {
            this.alertService.error(err);
          })
        } else {
          this.alertService.success("Успешно създадохте кауза!");
          this.goToCausesList();
        }
      }, err => {
        this.alertService.error("Каузата не може да бъде създадена: " + err);
      }
    );
  }
}
