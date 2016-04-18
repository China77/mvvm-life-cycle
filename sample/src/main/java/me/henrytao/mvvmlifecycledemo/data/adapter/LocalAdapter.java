/*
 * Copyright 2016 "Henry Tao <hi@henrytao.me>"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.henrytao.mvvmlifecycledemo.data.adapter;

import java.util.List;

import me.henrytao.mvvmlifecycledemo.data.model.Task;
import rx.Observable;

/**
 * Created by henrytao on 4/14/16.
 */
public interface LocalAdapter {

  void activeTask(String taskId);

  void completeTask(String taskId);

  Task createTask(String title, String description);

  Task findTask(String taskId);

  List<Task> getTasks();

  Observable<Task> observeTaskChange();

  Observable<Task> observeTaskCreate();

  Observable<Task> observeTaskRemove();

  Task removeTask(String taskId);

  Task updateTask(String taskId, String title, String description);
}
