package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

type Semaphore struct {
	sem chan struct{}
}

func (s *Semaphore) acquire() {
	s.sem <- struct{}{}
}

func (s *Semaphore) release() {
	<-s.sem
}

func new_semaphore(max_concurrency int) *Semaphore {
	return &Semaphore{
		sem: make(chan struct{}, max_concurrency),
	}
}

type Table struct {
	item1 string
	item2 string
}

type Smoker struct {
	index int
	item  string
}

func setRandomIngredients(table *Table) {
	randValue := rand.Intn(3)
	switch randValue {
	case 0:
		table.item1 = "Paper"
		table.item2 = "Matches"
		return
	case 1:
		table.item1 = "Tobacco"
		table.item2 = "Matches"
		return
	case 2:
		table.item1 = "Tobacco"
		table.item2 = "Paper"
		return
	}
}

func tableHasThisIngredient(smoker *Smoker, table *Table) bool {
	return smoker.item == table.item1 || smoker.item == table.item2
}

func tableIsEmpty(table *Table) bool {
	return table.item1 == "" && table.item2 == ""
}

func putIngredients(table *Table) {
	if tableIsEmpty(table) {
		var randValue = rand.Intn(3)
		switch randValue {
		case 0:
			fmt.Println("Put first ingredient: Paper")
			fmt.Println("Put second ingredient: Matches")
			table.item1 = "Paper"
			table.item2 = "Matches"
			return
		case 1:
			fmt.Println("Put first  ingredient: Tobacco")
			fmt.Println("Put second ingredient: Matches")
			table.item1 = "Tobacco"
			table.item2 = "Matches"
			return
		case 2:
			fmt.Println("Put first  ingredient: Tobacco")
			fmt.Println("Put second ingredient: Paper")
			table.item1 = "Tobacco"
			table.item2 = "Paper"
			return
		}
	}
}

func takeIngredients(smoker *Smoker, table *Table) bool {
	if tableHasThisIngredient(smoker, table) || tableIsEmpty(table) {
		return false
	} else {
		table.item1 = ""
		table.item2 = ""
		return true
	}
}

func smoking(smoker *Smoker, table *Table, semaphore *Semaphore, group *sync.WaitGroup) {
	semaphore.acquire()
	if takeIngredients(smoker, table) {
		fmt.Println("Smoker ", smoker.index, " are smoking")
		fmt.Println()
		time.Sleep(time.Second)
	}
	semaphore.release()
	group.Done()
}
func main() {
	now := time.Now()
	defer func() {
		fmt.Println(time.Since(now))
	}()
	semaphore := new_semaphore(1)
	smoker1 := Smoker{
		item:  "Tobacco",
		index: 1,
	}
	smoker2 := Smoker{
		item:  "Paper",
		index: 2,
	}
	smoker3 := Smoker{
		item:  "Matches",
		index: 3,
	}
	table := Table{}
	for {
		var beeper sync.WaitGroup
		beeper.Add(3)
		if tableIsEmpty(&table) {
			putIngredients(&table)
		}
		go smoking(&smoker1, &table, semaphore, &beeper)
		go smoking(&smoker2, &table, semaphore, &beeper)
		go smoking(&smoker3, &table, semaphore, &beeper)
		beeper.Wait()
	}

}
