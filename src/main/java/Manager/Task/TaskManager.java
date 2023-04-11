package Manager.Task;

import Manager.Manager;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TaskManager extends Manager {
    private final Map<Integer, Task> tasks = new HashMap<Integer, Task>();
    private final Map<Date, Set<Integer>> deadlines = new HashMap<Date, Set<Integer>>();

    private Boolean showComplete = Boolean.TRUE;

    private int numComplete = 0;

    @Override
    public void setPageSize(int size) {
        pageSize = size;
        numPages = tasks.size() / size + (tasks.size() % size == 0 ? 0 : 1);
    }

    public void addTask(String description, Date deadline, int priority) throws ArithmeticException {
        int id = tasks.size();
        if (id == Integer.MAX_VALUE) {
            throw new ArithmeticException("Max contacts reached");
        }
        tasks.put(id, new Task(id, description, deadline, priority));
        if (!deadlines.containsKey(deadline)) {
            deadlines.put(deadline, new HashSet<Integer>());
        }
        deadlines.get(deadline).add(id);
        if (numPages == 1) {
            ++pageSize;
        }
    }

    public void removeTask(int id) {
        deadlines.get(tasks.get(id).getDeadline()).remove(id);
        if (tasks.get(id).isComplete()) {
            --numComplete;
        }
        tasks.remove(id);
        if (numPages == 1) {
            --pageSize;
        }
    }
    public Task selectTask(int id) {
        return tasks.get(id);
    }

    public void markComplete(int id) {
        tasks.get(id).setComplete();
        ++numComplete;
    }

    public void markIncomplete(int id) {
        tasks.get(id).setIncomplete();
        --numComplete;
    }

    public void toggleComplete() {
        showComplete = !showComplete;
    }

    public Boolean getCompleteShown() {
        return showComplete;
    }

    public void showTasks() {
        if (tasks.size() - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        Integer[] keys = new Integer[tasks.size()];
        tasks.keySet().toArray(keys);
        Arrays.sort(keys, (fst, snd) -> {
            int deadlineDiff = tasks.get(fst).getDeadline().compareTo(tasks.get(snd).getDeadline());
            if (deadlineDiff != 0) {
                return deadlineDiff;
            }
            int priorityDiff = tasks.get(fst).getPriority() - tasks.get(snd).getPriority();
            if (priorityDiff != 0) {
                return priorityDiff;
            }
            return tasks.get(fst).getDescription().compareTo(tasks.get(snd).getDescription());
        });
        int start = currPage * pageSize;
        int end = Math.min((currPage + 1) * pageSize, tasks.size());
        int lastPageEls = tasks.size() % pageSize;
        System.out.printf("Page %d of %d | %d elements\n", currPage+1, numPages, currPage == numPages-1 ? (lastPageEls == 0 ? pageSize : lastPageEls): pageSize);
        for (int i = start; i < end; ++i) {
            if (i == 0) {
                System.out.println(StringUtils.repeat("#", 70));
            }
            Task currTask = tasks.get(keys[i]);
            if (currTask.isComplete() && !showComplete) {
                continue;
            }
            currTask.display();
            System.out.println(StringUtils.repeat("#", 70));
        }
        System.out.printf("Page %d of %d | %d elements\n", currPage+1, numPages, currPage == numPages-1 ? (lastPageEls == 0 ? pageSize : lastPageEls): pageSize);
    }

    public void showByDate(Date date) {
        int end = deadlines.get(date).size();
        if (end - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        Integer[] ids = new Integer[end];
        deadlines.get(date).toArray(ids);
        Arrays.sort(ids, (fst, snd) -> {
            int priorityDiff = tasks.get(fst).getPriority() - tasks.get(snd).getPriority();
            if (priorityDiff != 0) {
                return priorityDiff;
            }
            return tasks.get(fst).getDescription().compareTo(tasks.get(snd).getDescription());
        });
        for (int i = 0; i < end; ++i) {
            if (i == 0) {
                System.out.println(StringUtils.repeat("#", 70));
            }
            Task currTask = tasks.get(ids[i]);
            if (currTask.isComplete() && !showComplete) {
                continue;
            }
            currTask.display();
            System.out.println(StringUtils.repeat("#", 70));
        }
    }
}
